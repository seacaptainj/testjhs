package com.jeong2k.restapi.index;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jeong2k.restapi.events.EventController;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
public class IndexController {
	
	@GetMapping("/api")
	public ResourceSupport index() {
		var index = new ResourceSupport();
		index.add(linkTo(EventController.class).withRel("events"));
		return index;
	}
}
