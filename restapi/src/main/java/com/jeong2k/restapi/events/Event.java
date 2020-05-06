package com.jeong2k.restapi.events;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jeong2k.restapi.accounts.Account;
import com.jeong2k.restapi.accounts.AccountSerializer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of="id")
@Entity
public class Event {
	@Id @GeneratedValue
	private Integer id;
	private String name;
	private String description;
	
	private LocalDateTime beginEnrollmentDateTime;
	private LocalDateTime closeEnrollmentDateTime;
	private LocalDateTime beginEventDateTime;
	private LocalDateTime endEventDateTime;
	
	private String location;
	private int basePrice;
	private int maxPrice;
	private int limitOfEnrollment;
	private boolean offline;
	private boolean free;
	
	@ManyToOne
	@JsonSerialize(using = AccountSerializer.class)
	private Account manager;
	
	@Enumerated(EnumType.STRING)
	private EventStatus eventStatus = EventStatus.DRAFT;
	
	public void update() {
		// Update free
		if (this.basePrice == 0 && this.maxPrice == 0) {
			this.free = true;
		} else {
			this.free = false;
		}
		// Update offline
		if (this.location == null || this.location.isBlank()) {
			this.offline = false;
		} else {
			this.offline = true;
		}
	}
}