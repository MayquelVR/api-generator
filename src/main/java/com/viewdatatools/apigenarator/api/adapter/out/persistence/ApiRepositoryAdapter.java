package com.viewdatatools.apigenarator.api.adapter.out.persistence;

import com.viewdatatools.apigenarator.api.domain.model.ApiDomain;
import com.viewdatatools.apigenarator.api.domain.port.out.ApiRepositoryPort;
import com.viewdatatools.apigenarator.api.adapter.out.persistence.entity.ApiJpaEntity;
import com.viewdatatools.apigenarator.auth.adapter.out.persistence.entity.UserJpaEntity;
import com.viewdatatools.apigenarator.auth.adapter.out.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ApiRepositoryAdapter implements ApiRepositoryPort {

    private final ApiRepository apiRepository;
    private final UserJpaRepository userRepository;

    @Override
    public void save(ApiDomain apiDomain) {
        UserJpaEntity user = userRepository.findByUsername(apiDomain.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ApiJpaEntity jpaEntity = ApiJpaEntity.builder()
                .id(apiDomain.getId())
                .name(apiDomain.getName())
                .description(apiDomain.getDescription())
                .version(apiDomain.getVersion())
                .baseUrl(apiDomain.getBaseUrl())
                .method(apiDomain.getMethod())
                .requestFormat(apiDomain.getRequestFormat())
                .responseFormat(apiDomain.getResponseFormat())
                .route(apiDomain.getRoute())
                .user(user)
                .build();

        apiRepository.save(jpaEntity);
    }

    @Override
    public Optional<ApiDomain> findByUsernameAndRoute(String username, String route) {
        return apiRepository.findByUserUsernameAndRoute(username, route)
                .map(this::toDomainEntity);
    }

    @Override
    public List<ApiDomain> findAllByUserUsername(String username) {
        return apiRepository.findAllByUserUsername(username)
                .stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }

    private ApiDomain toDomainEntity(ApiJpaEntity jpaEntity) {
        return ApiDomain.builder()
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
