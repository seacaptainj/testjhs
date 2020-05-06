package com.jeong2k.restapi.events;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;


//public class EventResource extends ResourceSupport {
public class EventResource extends Resource<Event> {

	public EventResource(Event event, Link... links) {
		super(event, links);
		add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
	}
	
	/*
	 * @JsonUnwrapped private Event event;
	 * 
	 * public EventResource(Event event) { this.event = event; }
	 * 
	 * public Event getEvent() { return event; }
	 */
	
	
}