package dogapi;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Initializes the fetcher chain: Caching wraps the local test mock.
        // This setup is typically used for running unit tests quickly.
        BreedFetcher breedFetcher = new CachingBreedFetcher(new BreedFetcherForLocalTesting());

        String breed = "hound";
        int result = getNumberOfSubBreeds(breed, breedFetcher);
        System.out.println(breed + " has " + result + " sub breeds");

        breed = "cat"; // An invalid breed in the mock/API
        result = getNumberOfSubBreeds(breed, breedFetcher);
        System.out.println(breed + " has " + result + " sub breeds (expected 0)");
    }

    /**
     * Return the number of sub breeds that the given dog breed has according to the
     * provided fetcher.
     * @param breed the name of the dog breed
     * @param breedFetcher the breedFetcher to use
     * @return the number of sub breeds. Zero should be returned if there are no sub breeds
     * returned by the fetcher, including when the breed is not found (as required by MainTest).
     */
    public static int getNumberOfSubBreeds(String breed, BreedFetcher breedFetcher) {

        // A try-catch block is MANDATORY here to handle the checked BreedNotFoundException (Task 4)
        try {
            List<String> subBreeds = breedFetcher.getSubBreeds(breed);

            // Returns the size of the list (0 if no sub breeds exist for a valid breed)
            return subBreeds.size();

        } catch (BreedFetcher.BreedNotFoundException e) {
            // Catches the checked exception. Returning 0 satisfies the MainTest for invalid breeds.
            return 0;
        }
    }
}
