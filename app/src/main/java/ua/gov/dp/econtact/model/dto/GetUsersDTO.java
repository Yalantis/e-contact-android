package ua.gov.dp.econtact.model.dto;

import ua.gov.dp.econtact.model.User;

import java.util.List;

public class GetUsersDTO extends BaseDTO {

    private List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(final List<User> users) {
        this.users = users;
    }
}
