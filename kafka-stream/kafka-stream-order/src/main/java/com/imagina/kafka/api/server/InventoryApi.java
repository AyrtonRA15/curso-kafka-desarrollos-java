package com.imagina.kafka.api.server;

import com.imagina.kafka.api.command.service.InventoryService;
import com.imagina.kafka.api.request.InventoryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
public class InventoryApi {

	@Autowired
	private InventoryService service;

	@PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> add(@RequestBody InventoryRequest request) {
		service.addInventory(request);

		return ResponseEntity.ok().body("Added inventory : " + request.toString());
	}

	@PostMapping(value = "/subtract", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> subtract(@RequestBody InventoryRequest request) {
		service.subtractInventory(request);

		return ResponseEntity.ok().body("Removed inventory : " + request.toString());
	}

}
