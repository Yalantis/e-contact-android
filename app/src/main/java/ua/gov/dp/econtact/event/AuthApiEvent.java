package ua.gov.dp.econtact.event;


import ua.gov.dp.econtact.model.dto.AuthDTO;

public class AuthApiEvent extends ApiEvent<AuthDTO> {
    public AuthApiEvent(final AuthDTO data) {
        super(data);
    }
}
