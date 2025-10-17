package dogapi;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {

    // The underlying fetcher (e.g., DogApiBreedFetcher) that handles the actual API calls.
    private final BreedFetcher underlyingFetcher;

    // The cache: maps breed name (String) to its list of sub breeds (List<String>).
    private final Map<String, List<String>> cache;

    private int callsMade = 0;

    /**
     * Constructs a CachingBreedFetcher that wraps an underlying BreedFetcher.
     * @param fetcher The BreedFetcher instance to use for uncached requests.
     */
    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.underlyingFetcher = fetcher;
        this.cache = new HashMap<>();
    }

    @Override
    public List<String> getSubBreeds(String breed) {
        return List.of();
    }

    /**
     * Fetches the list of sub breeds. If the result is already in the cache, it is
     * returned immediately. Otherwise, the underlying fetcher is called, and a
     * successful result is stored in the cache.
     * * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedFetcher.BreedNotFoundException if the breed does not exist
     * (forwarded from the underlying fetcher)
     */
    @Override
    public List<String> fetchSubBreeds(String breed) throws BreedFetcher.BreedNotFoundException {
        // 1. Check the cache
        if (cache.containsKey(breed)) {
            return cache.get(breed);
        }

        // 2. Not in cache, so call the underlying fetcher
        try {
            // Increment the counter BEFORE making the call
            callsMade++;

            // Call the underlying fetcher (which may throw a checked exception)
            List<String> subBreeds = underlyingFetcher.fetchSubBreeds(breed);

            // 3. Successful result: cache the result and return it
            cache.put(breed, subBreeds);
            return subBreeds;

        } catch (BreedFetcher.BreedNotFoundException e) {
            // 4. Exception occurred: do NOT cache the failed result, just re-throw the exception
            throw e;
        }
    }

    /**
     * Returns the count of calls made to the underlying BreedFetcher.
     * @return the number of calls made
     */
    public int getCallsMade() {
        return callsMade;
    }
}