package se.umu.cs.ads.sp.model.communication.dto;

import java.util.ArrayList;

public class StartGameRequest {


    private ArrayList<EntitySkeletonDTO> entitySkeletons = new ArrayList<>();
    private ArrayList<CollectableDTO> collectables = new ArrayList<>();
    private ArrayList<EnvironmentDTO> environments = new ArrayList<>();

    public StartGameRequest(ArrayList<EntitySkeletonDTO> entitySkeletons, ArrayList<CollectableDTO> collectables, ArrayList<EnvironmentDTO> environments) {
        this.entitySkeletons = entitySkeletons;
        this.collectables = collectables;
        this.environments = environments;
    }

    public void addEntitySkeleton(EntitySkeletonDTO skel){
        entitySkeletons.add(skel);
    }

    public void addEnvironment(EnvironmentDTO env){
        environments.add(env);
    }

    public void addCollectable(CollectableDTO collectableDTO){
        collectables.add(collectableDTO);
    }

    public ArrayList<EntitySkeletonDTO> getEntitySkeletons() {
        return entitySkeletons;
    }

    public ArrayList<CollectableDTO> getCollectables() {
        return collectables;
    }

    public ArrayList<EnvironmentDTO> getEnvironments() {
        return environments;
    }

}
