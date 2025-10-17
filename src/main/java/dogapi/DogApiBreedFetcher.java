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
    private static final String API_BASE_URL = "https://dog.ceo/api/breed/";
    private final OkHttpClient client = new OkHttpClient();

    @Override
    public List<String> getSubBreeds(String breed) {
        return List.of();
    }

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> fetchSubBreeds(String breed) throws BreedFetcher.BreedNotFoundException {
        // Construct the API URL: https://dog.ceo/api/breed/{breed name}/list
        String url = API_BASE_URL + breed.toLowerCase() + "/list";

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {

            // 1. Check for unsuccessful response (e.g., 404)
            if (!response.isSuccessful()) {
                // The API documentation states that an invalid breed returns a 404,
                // but any non-successful code should be treated as a failure.
                throw new BreedFetcher.BreedNotFoundException("API call failed for breed: " + breed + " with code: " + response.code());
            }

            // Ensure the response body exists before reading
            if (response.body() == null) {
                throw new BreedFetcher.BreedNotFoundException("API call failed for breed: " + breed + ", response body was empty.");
            }

            String jsonString = response.body().string();
            JSONObject jsonObject = new JSONObject(jsonString);

            String status = jsonObject.getString("status");

            // 2. Check the JSON status field for an error
            if ("error".equals(status)) {
                String message = jsonObject.getString("message");
                // "Breed not found (main breed does not exist)" indicates the breed is invalid
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
            // 4. Handle networking/IO issues (e.g., no internet, bad connection)
            // As per the requirement: all failures are reported as BreedNotFoundException
            throw new BreedFetcher.BreedNotFoundException("Network or API communication error for breed: " + breed + ". Details: " + e.getMessage());
        } catch (Exception e) {
            // 5. Catch all other exceptions, e.g., JSON parsing errors
            throw new BreedFetcher.BreedNotFoundException("Data processing error for breed: " + breed + ". Details: " + e.getMessage());
        }
    }
}