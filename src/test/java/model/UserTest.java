// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package model;

import it.bz.beacon.api.db.model.User;
import it.bz.beacon.api.model.UserCreation;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class UserTest {

    @Test
    public void whenUserCreation_thenUserShouldBeCreated() {
        UserCreation creation = new UserCreation();
        creation.setUsername("utest");
        creation.setName("Max");
        creation.setSurname("Muster");
        creation.setEmail("max.muster@test.com");
        creation.setPassword("123456");

        User user = User.create(creation);

        assertEquals(creation.getUsername(), user.getUsername());
        assertEquals(creation.getName(), user.getName());
        assertEquals(creation.getSurname(), user.getSurname());
        assertEquals(creation.getEmail(), user.getEmail());
        assertEquals(creation.getPassword(), user.getPassword());
    }

}
