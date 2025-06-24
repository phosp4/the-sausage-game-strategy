package io.github.data;

public class GameRepositoryProvider {
    private static GameRepository repository = new DummyGameRepository();

    public static void setRepository(GameRepository repo) {
        repository = repo;
    }

    public static GameRepository getRepository() {
        return repository;
    }
}
