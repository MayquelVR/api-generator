package com.viewdatatools.apigenarator.api.adapter.in.web;

import com.viewdatatools.apigenarator.api.adapter.in.web.mapper.ApiDtoMapper;
import com.viewdatatools.apigenarator.api.domain.port.in.CreateApiUseCase;
import com.viewdatatools.apigenarator.api.domain.port.in.InvokeApiUseCase;
import com.viewdatatools.apigenarator.api.domain.port.in.ListUserApisUseCase;
import com.viewdatatools.apigenarator.api.dto.ApiCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiRestController {

    private final CreateApiUseCase createApiUseCase;
    private final InvokeApiUseCase invokeApiUseCase;
    private final ApiDtoMapper apiDtoMapper;
    private final ListUserApisUseCase listUserApisUseCase;

    @PostMapping
    public ResponseEntity<?> createApi(@RequestBody ApiCreateRequest req) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        createApiUseCase.createApi(apiDtoMapper.toDomain(req, username));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{username}/{route}")
    public ResponseEntity<?> getApi(@PathVariable String username, @PathVariable String route) {
        Object response = invokeApiUseCase.invokeApi(username, route, "GET", null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{username}/{route}")
    public ResponseEntity<?> postApi(@PathVariable String username, @PathVariable String route, @RequestBody(required = false) Object body) {
        Object response = invokeApiUseCase.invokeApi(username, route, "POST", body);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{username}/{route}")
    public ResponseEntity<?> putApi(@PathVariable String username, @PathVariable String route, @RequestBody(required = false) Object body) {
        Object response = invokeApiUseCase.invokeApi(username, route, "PUT", body);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{username}/{route}")
    public ResponseEntity<?> deleteApi(@PathVariable String username, @PathVariable String route) {
        Object response = invokeApiUseCase.invokeApi(username, route, "DELETE", null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}/list")
    public ResponseEntity<?> listUserApis(@PathVariable String username) {
        return ResponseEntity.ok(listUserApisUseCase.listApisByUsername(username));
    }
}
