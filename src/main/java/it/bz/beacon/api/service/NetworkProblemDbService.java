package it.bz.beacon.api.service;

import it.bz.beacon.api.db.repository.NetworkProblemRepository;
import it.bz.beacon.api.exception.NetworkProblemNotFoundException;
import it.bz.beacon.api.model.Beacon;
import it.bz.beacon.api.db.model.NetworkProblem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NetworkProblemDbService implements INetworkProblemService {

    @Autowired
    private NetworkProblemRepository repository;

    @Override
    public List<NetworkProblem> findAll() {
        return repository.findAll();
    }

    @Override
    public List<NetworkProblem> findAllForBeacon(Beacon beacon) {
        return repository.findByBeaconId(beacon.getId());
    }

    @Override
    public NetworkProblem findForBeacon(Beacon beacon, long id) throws NetworkProblemNotFoundException {
        return repository.findById(id).orElseThrow(NetworkProblemNotFoundException::new);
    }

    @Override
    public NetworkProblem createForBeacon(Beacon beacon, NetworkProblem networkProblem) {
        return repository.save(networkProblem);
    }

    @Override
    public NetworkProblem updateForBeacon(Beacon beacon, long id, NetworkProblem networkProblemRequest) throws NetworkProblemNotFoundException {
        return repository.findById(id).map(
                networkProblem -> {
                    networkProblem.setBeaconId(networkProblemRequest.getBeaconId());
                    networkProblem.setDescription(networkProblemRequest.getDescription());

                    return repository.save(networkProblem);
                }).orElseThrow(NetworkProblemNotFoundException::new);
    }

    @Override
    public ResponseEntity<?> deleteForBeacon(Beacon beacon, long id) throws NetworkProblemNotFoundException {
        return repository.findById(id).map(
                networkProblem -> {
                    repository.delete(networkProblem);
                    return ResponseEntity.ok().build();
        }).orElseThrow(NetworkProblemNotFoundException::new);

    }
}
