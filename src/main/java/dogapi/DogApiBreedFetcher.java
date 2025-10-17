package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();
    private static final String API_BASE_URL = "https://dog.ceo/api/breed/";

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedFetcher.BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedFetcher.BreedNotFoundException {
        // Build the URL, ensuring the breed name is lowercased for the API endpoint
        String url = API_BASE_URL + breed.toLowerCase() + "/list";

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {

            // 1. Check for unsuccessful HTTP status code (e.g., 404)
            if (!response.isSuccessful()) {
                throw new BreedFetcher.BreedNotFoundException("API call failed for breed: " + breed + " with code: " + response.code());
            }

            if (response.body() == null) {
                throw new BreedFetcher.BreedNotFoundException("API call failed for breed: " + breed + ", response body was empty.");
            }

            String jsonString = response.body().string();
            JSONObject jsonObject = new JSONObject(jsonString);

            String status = jsonObject.getString("status");

            // 2. Check the JSON status field for an explicit error message
            if ("error".equals(status)) {
                String message = jsonObject.getString("message");
                // Throws the checked exception
                throw new BreedFetcher.BreedNotFoundException("Breed not found: " + breed + ". API message: " + message);
            }

            // 3. Status is "success", extract the list of sub-breeds from the "message" field
            if ("success".equals(status)) {
                JSONArray messageArray = jsonObject.getJSONArray("message");
                List<String> subBreeds = new ArrayList<>();
                for (int i = 0; i < messageArray.length(); i++) {
                    subBreeds.add(messageArray.getString(i));
                }
                return subBreeds;
            }

            // Fallback for unexpected API response structure
            throw new BreedFetcher.BreedNotFoundException("Unexpected API response for breed: " + breed);

        } catch (IOException e) {
            // Handle network/IO issues by wrapping them in the checked exception
            throw new BreedFetcher.BreedNotFoundException("Network or API communication error for breed: " + breed + ". Details: " + e.getMessage());
        } catch (Exception e) {
            // Catch all other errors, like JSON parsing failures
            throw new BreedFetcher.BreedNotFoundException("Data processing error for breed: " + breed + ". Details: " + e.getMessage());
        }
    }

    @Override
    public List<String> fetchSubBreeds(String breed) throws BreedNotFoundException {
        return List.of();
    }
}
