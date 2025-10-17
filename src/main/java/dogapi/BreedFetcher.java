package dogapi;

import java.util.List;

/**
 * Interface for the service of getting sub breeds of a given dog breed.
 */
public interface BreedFetcher {

    /**
     * Fetch the list of sub breeds for the given breed.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist
     */
    List<String> getSubBreeds(String breed) throws BreedNotFoundException;

    List<String> fetchSubBreeds(String breed) throws BreedNotFoundException;


    // CRITICAL CHANGE: Extends 'Exception' instead of RuntimeException
    // to make it a checked exception (Task 4).
    class BreedNotFoundException extends Exception {
        public BreedNotFoundException(String message) {
            super(message);
        }
    }
}
