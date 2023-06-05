// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.kontakt.io.model;

import it.bz.beacon.api.kontakt.io.model.enumeration.Packet;
import it.bz.beacon.api.kontakt.io.model.enumeration.Profile;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public abstract class BeaconDevice extends Device {
    private String name;
    private String mac;
    private Set<Profile> profiles;
    private Set<Packet> packets;
    private UUID proximity;
    private int major;
    private int minor;
    private String namespace;
    private String instanceId;
    private String url;
    private boolean shuffled;
    private UUID secureProximity;
    private String secureNamespace;
    private FutureId futureId;
    private int interval;
    private Set<Integer> rss1m;
    private Set<Integer> rss0m;
    private Object customConfiguration;
    private long lastSeen;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Set<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(Set<Profile> profiles) {
        this.profiles = profiles;
    }

    public Set<Packet> getPackets() {
        return packets;
    }

    public void setPackets(Set<Packet> packets) {
        this.packets = packets;
    }

    public UUID getProximity() {
        return proximity;
    }

    public void setProximity(UUID proximity) {
        this.proximity = proximity;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isShuffled() {
        return shuffled;
    }

    public void setShuffled(boolean shuffled) {
        this.shuffled = shuffled;
    }

    public UUID getSecureProximity() {
        return secureProximity;
    }

    public void setSecureProximity(UUID secureProximity) {
        this.secureProximity = secureProximity;
    }

    public String getSecureNamespace() {
        return secureNamespace;
    }

    public void setSecureNamespace(String secureNamespace) {
        this.secureNamespace = secureNamespace;
    }

    public FutureId getFutureId() {
        return futureId;
    }

    public void setFutureId(FutureId futureId) {
        this.futureId = futureId;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public Set<Integer> getRss1m() {
        return rss1m;
    }

    public void setRss1m(Set<Integer> rss1m) {
        this.rss1m = rss1m;
    }

    public Set<Integer> getRss0m() {
        return rss0m;
    }

    public void setRss0m(Set<Integer> rss0m) {
        this.rss0m = rss0m;
    }

    public Object getCustomConfiguration() {
        return customConfiguration;
    }

    public void setCustomConfiguration(Object customConfiguration) {
        this.customConfiguration = customConfiguration;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public static class FutureId {

        private List<FutureIBeacon> iBeacon;
        private List<FutureEddystone> eddystone;

        public List<FutureIBeacon> getiBeacon() {
            return iBeacon;
        }

        public void setiBeacon(List<FutureIBeacon> iBeacon) {
            this.iBeacon = iBeacon;
        }

        public List<FutureEddystone> getEddystone() {
            return eddystone;
        }

        public void setEddystone(List<FutureEddystone> eddystone) {
            this.eddystone = eddystone;
        }

        public static class FutureIBeacon {
            private UUID proximity;
            private int major;
            private int minor;

            public UUID getProximity() {
                return proximity;
            }

            public void setProximity(UUID proximity) {
                this.proximity = proximity;
            }

            public int getMajor() {
                return major;
            }

            public void setMajor(int major) {
                this.major = major;
            }

            public int getMinor() {
                return minor;
            }

            public void setMinor(int minor) {
                this.minor = minor;
            }
        }

        public static class FutureEddystone {
            private String namespace;
            private String instanceId;

            public String getNamespace() {
                return namespace;
            }

            public void setNamespace(String namespace) {
                this.namespace = namespace;
            }

            public String getInstanceId() {
                return instanceId;
            }

            public void setInstanceId(String instanceId) {
                this.instanceId = instanceId;
            }
        }
    }
}
