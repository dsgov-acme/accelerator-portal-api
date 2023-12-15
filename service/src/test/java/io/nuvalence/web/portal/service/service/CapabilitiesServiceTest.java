package io.nuvalence.web.portal.service.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import io.nuvalence.auth.token.UserToken;
import io.nuvalence.web.portal.service.domain.Capability;
import io.nuvalence.web.portal.service.domain.CapabilityApplicationRole;
import io.nuvalence.web.portal.service.repository.CapabilityApplicationRoleRepository;
import io.nuvalence.web.portal.service.utils.auth.CurrentUserUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CapabilitiesServiceTest {
    private CapabilitiesService service;

    private CapabilityApplicationRoleRepository repository;

    @BeforeEach
    public void setUp() {
        repository = mock(CapabilityApplicationRoleRepository.class);
        service = new CapabilitiesService(repository);
    }

    @Test
    void getCapabilitiesForCurrentUserDelegatesToCapabilityApplicationRoleRepository() {
        try (MockedStatic<CurrentUserUtility> mockedCurrentUserUtility =
                mockStatic(CurrentUserUtility.class)) {
            UserToken mockUserToken = mock(UserToken.class);
            when(mockUserToken.getAllRoles()).thenReturn(List.of("role1", "role2"));
            when(CurrentUserUtility.getCurrentUser()).thenReturn(Optional.of(mockUserToken));

            CapabilityApplicationRole capabilityOne =
                    CapabilityApplicationRole.builder()
                            .applicationRole("role1")
                            .capability(
                                    Capability.builder()
                                            .id(UUID.randomUUID())
                                            .key("capability1")
                                            .build())
                            .build();
            CapabilityApplicationRole capabilityTwo =
                    CapabilityApplicationRole.builder()
                            .applicationRole("role2")
                            .capability(
                                    Capability.builder()
                                            .id(UUID.randomUUID())
                                            .key("capability2")
                                            .build())
                            .build();
            when(repository.findByApplicationRoleIn(List.of("role1", "role2")))
                    .thenReturn(List.of(capabilityOne, capabilityTwo));

            List<Capability> result = service.getCapabilitiesForCurrentUser();

            assertTrue(result.contains(capabilityOne.getCapability()));
            assertTrue(result.contains(capabilityTwo.getCapability()));
        }
    }
}
