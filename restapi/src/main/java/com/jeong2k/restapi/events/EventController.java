package com.jeong2k.restapi.events;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.net.URI;
import java.util.Optional;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jeong2k.restapi.accounts.Account;
import com.jeong2k.restapi.accounts.CurrentUser;
import com.jeong2k.restapi.common.ErrorsResource;


@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {
	
	private final EventRepository eventRepository;
	private final ModelMapper modelMapper;
	private final EventValidator eventValidator;
	
	
	public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
		this.eventRepository = eventRepository;
		this.modelMapper = modelMapper;
		this.eventValidator =  eventValidator;
	}
	
	@GetMapping("/{id}")
	public ResponseEntity getEvent(@PathVariable Integer id, @CurrentUser Account currentUser) {
		Optional<Event> optionalEvent = this.eventRepository.findById(id);
		if (optionalEvent.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		Event event = optionalEvent.get();
		EventResource eventResource = new EventResource(event);
		
		if((event.getManager() != null) && (event.getManager().equals(currentUser))) {
			eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
		}
		return ResponseEntity.ok(eventResource);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity updateEvent(@PathVariable Integer id, @RequestBody @Valid EventDto eventDto, Errors errors, @CurrentUser Account currentUser) {
		
		
		Optional<Event> optionalEvent = this.eventRepository.findById(id);
		if (optionalEvent.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		//필수 입력 체크, 최소값 체크 
		if (errors.hasErrors()) {
			return badRequest(errors);
		}
		
		//입력받은 항목을 체크
		this.eventValidator.validate(eventDto, errors);
		if (errors.hasErrors()) {
			return badRequest(errors);
		}
		
		//입력 받은 EventDto와 DB에서 읽어온 Event을 맵핑
		Event existingEvent = optionalEvent.get();
		this.modelMapper.map(eventDto, existingEvent);
		Event savedEvent = this.eventRepository.save(existingEvent);
		//Update된 Event를 EventResource로 Wrapping해서 Response Body에 전달한다. 
		EventResource eventResource = new EventResource(savedEvent);
		
		if ((existingEvent.getManager() != null) && (!existingEvent.getManager().equals(currentUser))) {
			return new ResponseEntity(HttpStatus.UNAUTHORIZED);
		}
		
		return ResponseEntity.ok(eventResource);
	}

	
	@GetMapping
	public ResponseEntity queryEvents(Pageable pageable
			, PagedResourcesAssembler<Event> assembler
			,@CurrentUser Account currentUser
			//, @AuthenticationPrincipal(expression = "account") Account currentUser
			//, @AuthenticationPrincipal AccountAdapter currentUser
			) {
		Page<Event> page = this.eventRepository.findAll(pageable);
		PagedResources<Resource<Event>> pagedResources =
		assembler.toResource(page, event -> new EventResource(event));
		//로그인한 상태면 event를 create하는 링크를 추가해 준다. 
		if(currentUser != null) {
			pagedResources.add(linkTo(EventController.class).withRel("create-event"));
		}
		
		return ResponseEntity.ok(pagedResources);
	}

	@PostMapping
	public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors, @CurrentUser Account curruntUser) {
		//입력필드 필수, 최소값 체크
		if(errors.hasErrors()) {
			return badRequest(errors);
		}
		//입력필드 로직 체크 
		eventValidator.validate(eventDto, errors);
		if(errors.hasErrors()) {
			return badRequest(errors);
		}
		
		//Dto, Entity 연결
		Event event = modelMapper.map(eventDto, Event.class);
		//free, offline 필드값 처리
		event.update();
		event.setManager(curruntUser);
		
		//JPA 테이블 생성
		Event addEvent = this.eventRepository.save(event);
		/*
		URI createUri = linkTo(EventController.class).slash(addEvent.getId()).toUri();
		return ResponseEntity.created(createUri).body(addEvent);
		*/
		ControllerLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(addEvent.getId());
		URI createUri = selfLinkBuilder.toUri();
		
		//new Resource<Event>().add(selfLinkBuilder.withSelfRel());
		
		EventResource eventResource = new EventResource(event);
		eventResource.add(linkTo(EventController.class).withRel("query-events"));
		//eventResource.add(selfLinkBuilder.withSelfRel());
		eventResource.add(selfLinkBuilder.withRel("update-event"));
		
		return ResponseEntity.created(createUri).body(eventResource);
	}

	private ControllerLinkBuilder selfLinkBuilder(Event addEvent) {
		return linkTo(EventController.class).slash(addEvent.getId());
	}

	private ResponseEntity badRequest(Errors errors) {
		//return ResponseEntity.badRequest().body(errors);
		return ResponseEntity.badRequest().body(new ErrorsResource(errors));
	}
}
