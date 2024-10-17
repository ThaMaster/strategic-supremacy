package se.umu.cs.ads.sp.model.communication.dto;

import java.util.ArrayList;

public record StartGameRequestDTO(ArrayList<EntitySkeletonDTO> entitySkeletons,
                                  ArrayList<CollectableDTO> collectables,
                                  ArrayList<EnvironmentDTO> environments) {

    public void addEntitySkeleton(EntitySkeletonDTO skeleton) {
        entitySkeletons.add(skeleton);
    }

    public void addEnvironment(EnvironmentDTO env) {
        environments.add(env);
    }

    public void addCollectable(CollectableDTO collectableDTO) {
        collectables.add(collectableDTO);
    }

}
