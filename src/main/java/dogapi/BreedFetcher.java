package dogapi;

import java.util.List;

/**
 * Interface for the service of getting sub breeds of a given dog breed.
 */
public interface BreedFetcher {

    List<String> getSubBreeds(String breed) throws BreedNotFoundException;

    /**
     * Fetch the list of sub breeds for the given breed.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist
     */
    // CRITICAL CHANGE: The method signature must declare the checked exception.
    List<String> fetchSubBreeds(String breed) throws BreedNotFoundException;


    // CRITICAL CHANGE: The exception must now extend 'Exception' (instead of RuntimeException)
    // to become a checked exception.
    class BreedNotFoundException extends Exception {
        public BreedNotFoundException(String message) {
            super(message);
        }
    }
}