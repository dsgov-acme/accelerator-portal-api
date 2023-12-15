package io.nuvalence.web.portal.service.service;

import io.nuvalence.auth.token.UserToken;
import io.nuvalence.web.portal.service.domain.Capability;
import io.nuvalence.web.portal.service.domain.CapabilityApplicationRole;
import io.nuvalence.web.portal.service.repository.CapabilityApplicationRoleRepository;
import io.nuvalence.web.portal.service.utils.auth.CurrentUserUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing capabilities.
 */
@Service
@RequiredArgsConstructor
public class CapabilitiesService {
    private final CapabilityApplicationRoleRepository capabilityApplicationRoleRepository;

    /**
     * Fetches the capabilities for the current user.
     *
     * @return List of capabilities.
     */
    public List<Capability> getCapabilitiesForCurrentUser() {
        List<String> roles =
                CurrentUserUtility.getCurrentUser().map(UserToken::getAllRoles).orElse(null);

        return capabilityApplicationRoleRepository.findByApplicationRoleIn(roles).stream()
                .map(CapabilityApplicationRole::getCapability)
                .collect(Collectors.toList());
    }
}
