package com.viewdatatools.apigenarator.api.adapter.out.persistence;

import com.viewdatatools.apigenarator.api.domain.model.Api;
import com.viewdatatools.apigenarator.api.domain.port.out.ApiRepositoryPort;
import com.viewdatatools.apigenarator.api.adapter.out.persistence.entity.ApiJpaEntity;
import com.viewdatatools.apigenarator.auth.model.User;
import com.viewdatatools.apigenarator.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ApiRepositoryAdapter implements ApiRepositoryPort {

    private final ApiRepository apiRepository;
    private final UserRepository userRepository;

    @Override
    public void save(Api api) {
        User user = userRepository.findByUsername(api.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ApiJpaEntity jpaEntity = ApiJpaEntity.builder()
                .id(api.getId())
                .name(api.getName())
                .description(api.getDescription())
                .version(api.getVersion())
                .baseUrl(api.getBaseUrl())
                .method(api.getMethod())
                .requestFormat(api.getRequestFormat())
                .responseFormat(api.getResponseFormat())
                .route(api.getRoute())
                .user(user)
                .build();

        apiRepository.save(jpaEntity);
    }

    @Override
    public Optional<Api> findByUsernameAndRoute(String username, String route) {
        return apiRepository.findByUserUsernameAndRoute(username, route)
                .map(this::toDomainEntity);
    }

    @Override
    public List<Api> findAllByUserUsername(String username) {
        return apiRepository.findAllByUserUsername(username)
                .stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }

    private Api toDomainEntity(ApiJpaEntity jpaEntity) {
        return Api.builder()
                .id(jpaEntity.getId())
                .name(jpaEntity.getName())
                .description(jpaEntity.getDescription())
                .version(jpaEntity.getVersion())
                .baseUrl(jpaEntity.getBaseUrl())
                .method(jpaEntity.getMethod())
                .requestFormat(jpaEntity.getRequestFormat())
                .responseFormat(jpaEntity.getResponseFormat())
                .route(jpaEntity.getRoute())
                .username(jpaEntity.getUser().getUsername())
                .build();
    }
}
