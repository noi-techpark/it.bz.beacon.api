package it.bz.beacon.api.service;

import it.bz.beacon.api.exception.NetworkProblemNotFoundException;
import it.bz.beacon.api.model.Beacon;
import it.bz.beacon.api.db.model.NetworkProblem;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface INetworkProblemService {
    List<NetworkProblem> findAll();
    List<NetworkProblem> findAllForBeacon(Beacon beacon);
    NetworkProblem findForBeacon(Beacon beacon, long id) throws NetworkProblemNotFoundException;
    NetworkProblem createForBeacon(Beacon beacon, NetworkProblem networkProblem);
    NetworkProblem updateForBeacon(Beacon beacon, long id, NetworkProblem networkProblem) throws NetworkProblemNotFoundException;
    ResponseEntity<?> deleteForBeacon(Beacon beacon, long id) throws NetworkProblemNotFoundException;
}
