package se.umu.cs.ads.sp.model.communication.dto;

import java.util.ArrayList;

public record StartGameRequestDTO(
        ArrayList<UsersEntitiesDTO> entitySkeletons,
        ArrayList<CollectableDTO> collectables,
        ArrayList<EnvironmentDTO> environments
) {

    public void addEnvironment(EnvironmentDTO env) {
        environments.add(env);
    }

    public void addCollectable(CollectableDTO collectableDTO) {
        collectables.add(collectableDTO);
    }

    public void addSkeleton(UsersEntitiesDTO skeletons) {
        entitySkeletons.add(skeletons);
    }

}
