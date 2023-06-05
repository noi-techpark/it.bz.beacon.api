// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.model;

import it.bz.beacon.api.db.model.Group;

public class GroupApiKey {
    private long groupId;
    private String apiKey;

    public GroupApiKey() {
    }

    public static GroupApiKey fromGroup(Group group) {
        GroupApiKey groupApiKey = new GroupApiKey();
        groupApiKey.groupId = group.getId();
        groupApiKey.apiKey = group.getKontaktIoApiKey();

        return groupApiKey;
    }

    public long getGroupId() {
        return groupId;
    }

    public String getApiKey() {
        return apiKey;
    }
}
