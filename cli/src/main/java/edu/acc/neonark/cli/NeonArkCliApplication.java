package edu.acc.neonark.cli;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class NeonArkCliApplication {
    private final Scanner scanner = new Scanner(System.in);
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String baseUrl;

    public NeonArkCliApplication(String baseUrl) {
        this.baseUrl = trimTrailingSlash(baseUrl);
    }

    public static void main(String[] args) {
        String configuredUrl = args.length > 0
                ? args[0]
                : System.getenv().getOrDefault("NEON_ARK_API_URL", "http://localhost:8080");
        new NeonArkCliApplication(configuredUrl).run();
    }

    private void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String option = scanner.nextLine().trim();
            try {
                switch (option) {
                    case "1" -> listCreatures();
                    case "2" -> viewCreatureById();
                    case "3" -> registerCreature();
                    case "4" -> renameCreature();
                    case "5" -> removeCreature();
                    case "6" -> viewCreatureObservations();
                    case "7" -> findCreaturesByFeedingTime();
                    case "8" -> viewAllSystemUsers();
                    case "0" -> running = !confirm("Are you sure you want to exit?");
                    default -> System.out.println("Invalid option. Enter one of the listed menu numbers.");
                }
            } catch (IOException | InterruptedException ex) {
                if (ex instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("Request failed. Confirm the backend is running at " + baseUrl + ".");
            }
            System.out.println();
        }
        System.out.println("Goodbye.");
    }

    private void printMenu() {
        System.out.println("=====================================");
        System.out.println("NEON ARK CLI SYSTEM");
        System.out.println("=====================================");
        System.out.println("1. List all creatures");
        System.out.println("2. View creature by ID");
        System.out.println("3. Register new creature");
        System.out.println("4. Rename creature");
        System.out.println("5. Remove creature");
        System.out.println("6. View creature observations/notes");
        System.out.println("7. Find creatures by feeding time");
        System.out.println("--- Admin Only ---");
        System.out.println("8. View all system users");
        System.out.println("0. Exit");
        System.out.println("-------------------------------------");
        System.out.print("Select an option: ");
    }

    private void listCreatures() throws IOException, InterruptedException {
        boolean includeRemoved = promptYesNo("Include removed creatures? (Y/N, default N): ");
        String path = includeRemoved ? "/api/creatures?includeRemoved=true" : "/api/creatures";
        HttpResponse<String> response = send("GET", path, null, Map.of());
        if (!isSuccess(response)) {
            printError(response);
            return;
        }
        List<CreatureResponse> creatures = objectMapper.readValue(
                response.body(),
                new TypeReference<>() {
                }
        );
        printCreatureTable(creatures);
    }

    private void viewCreatureById() throws IOException, InterruptedException {
        Long id = promptLong("Creature ID: ");
        if (id == null) {
            return;
        }
        HttpResponse<String> response = send("GET", "/api/creatures/" + id, null, Map.of());
        if (!isSuccess(response)) {
            printError(response);
            return;
        }
        printCreatureDetails(objectMapper.readValue(response.body(), CreatureResponse.class));
    }

    private void registerCreature() throws IOException, InterruptedException {
        String name = promptRequired("Creature name: ");
        if (name == null) {
            return;
        }
        String species = promptRequired("Species: ");
        if (species == null) {
            return;
        }
        Long habitatId = promptLong("Habitat ID: ");
        if (habitatId == null) {
            return;
        }
        System.out.print("Status [ACTIVE/INTAKE, blank for ACTIVE]: ");
        String status = scanner.nextLine().trim().toUpperCase(Locale.ROOT);
        if (!status.isBlank() && !status.equals("ACTIVE") && !status.equals("INTAKE")) {
            System.out.println("Invalid status. Use ACTIVE, INTAKE, or leave blank.");
            return;
        }

        CreateCreatureRequest request = new CreateCreatureRequest(name, species, habitatId, status.isBlank() ? null : status);
        HttpResponse<String> response = send("POST", "/api/creatures", request, Map.of());
        if (!isSuccess(response)) {
            printError(response);
            return;
        }
        System.out.println("Creature registered.");
        printCreatureDetails(objectMapper.readValue(response.body(), CreatureResponse.class));
    }

    private void renameCreature() throws IOException, InterruptedException {
        Long id = promptLong("Creature ID to rename: ");
        if (id == null) {
            return;
        }
        String newName = promptRequired("New creature name: ");
        if (newName == null) {
            return;
        }
        if (!confirm("Rename creature " + id + " to \"" + newName + "\"?")) {
            System.out.println("Rename canceled. No API request was sent.");
            return;
        }

        HttpResponse<String> response = send("PUT", "/api/creatures/" + id + "/name", new RenameCreatureRequest(newName), Map.of());
        if (!isSuccess(response)) {
            printError(response);
            return;
        }
        RenameCreatureResponse renamed = objectMapper.readValue(response.body(), RenameCreatureResponse.class);
        System.out.printf("%-14s %s%n", "Old name:", renamed.oldName());
        System.out.printf("%-14s %s%n", "New name:", renamed.newName());
        System.out.printf("%-14s %s%n", "Habitat:", renamed.habitatName());
        System.out.println(renamed.message());
    }

    private void removeCreature() throws IOException, InterruptedException {
        Long id = promptLong("Creature ID to remove: ");
        if (id == null) {
            return;
        }
        if (!confirm("Remove creature " + id + " from active operations?")) {
            System.out.println("Removal canceled. No API request was sent.");
            return;
        }

        HttpResponse<String> response = send("DELETE", "/api/creatures/" + id, null, Map.of());
        if (!isSuccess(response)) {
            printError(response);
            return;
        }
        DeleteCreatureResponse removed = objectMapper.readValue(response.body(), DeleteCreatureResponse.class);
        System.out.printf("%-14s %d%n", "Creature ID:", removed.id());
        System.out.printf("%-14s %s%n", "Name:", removed.name());
        System.out.printf("%-14s %s%n", "Status:", removed.status());
        System.out.printf("%-14s %s%n", "Removed at:", removed.removedAt());
        System.out.println(removed.message());
    }

    private void viewCreatureObservations() throws IOException, InterruptedException {
        Long id = promptLong("Creature ID: ");
        if (id == null) {
            return;
        }
        HttpResponse<String> response = send("GET", "/api/creatures/" + id + "/observations", null, Map.of());
        if (!isSuccess(response)) {
            printError(response);
            return;
        }
        CreatureObservationsResponse observations = objectMapper.readValue(response.body(), CreatureObservationsResponse.class);
        printCreatureDetails(observations.creature());
        printObservationsTable(observations.observations());
    }

    private void findCreaturesByFeedingTime() throws IOException, InterruptedException {
        System.out.print("Feeding time (HH:MM): ");
        String time = scanner.nextLine().trim();
        if (!time.matches("\\d{2}:\\d{2}")) {
            System.out.println("Invalid time. Use HH:MM format, such as 08:00.");
            return;
        }
        String encoded = URLEncoder.encode(time, StandardCharsets.UTF_8);
        HttpResponse<String> response = send("GET", "/api/feedings?time=" + encoded, null, Map.of());
        if (!isSuccess(response)) {
            printError(response);
            return;
        }
        FeedingLookupResponse lookup = objectMapper.readValue(response.body(), FeedingLookupResponse.class);
        System.out.println(lookup.message());
        printFeedingTable(lookup.feedings());
    }

    private void viewAllSystemUsers() throws IOException, InterruptedException {
        System.out.print("Admin role header value (ADMIN required, blank for none): ");
        String role = scanner.nextLine().trim();
        Map<String, String> headers = role.isBlank() ? Map.of() : Map.of("X-Role", role);

        HttpResponse<String> response = send("GET", "/api/admin/users", null, headers);
        if (!isSuccess(response)) {
            printError(response);
            return;
        }
        List<UserResponse> users = objectMapper.readValue(
                response.body(),
                new TypeReference<>() {
                }
        );
        printUserTable(users);
    }

    private HttpResponse<String> send(String method, String path, Object body, Map<String, String> headers)
            throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(baseUrl + path))
                .header("Accept", "application/json");
        headers.forEach(builder::header);

        if (body == null) {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        } else {
            builder.header("Content-Type", "application/json");
            builder.method(method, HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)));
        }
        return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private boolean isSuccess(HttpResponse<String> response) {
        return response.statusCode() >= 200 && response.statusCode() < 300;
    }

    private void printError(HttpResponse<String> response) throws IOException {
        if (response.body() == null || response.body().isBlank()) {
            System.out.println("HTTP " + response.statusCode() + " returned without details.");
            return;
        }
        ErrorResponse error = objectMapper.readValue(response.body(), ErrorResponse.class);
        System.out.println("HTTP " + error.status() + " - " + error.error() + ": " + error.message());
        if (error.fields() != null && !error.fields().isEmpty()) {
            error.fields().forEach((field, message) -> System.out.println("  " + field + ": " + message));
        }
    }

    private Long promptLong(String prompt) {
        System.out.print(prompt);
        String value = scanner.nextLine().trim();
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            System.out.println("Invalid number. Enter a whole-number ID.");
            return null;
        }
    }

    private String promptRequired(String prompt) {
        System.out.print(prompt);
        String value = scanner.nextLine().trim();
        if (value.isBlank()) {
            System.out.println("Value is required.");
            return null;
        }
        return value;
    }

    private boolean confirm(String message) {
        System.out.print(message + " (Y/N): ");
        String value = scanner.nextLine().trim();
        return value.equalsIgnoreCase("Y") || value.equalsIgnoreCase("YES");
    }

    private boolean promptYesNo(String prompt) {
        System.out.print(prompt);
        String value = scanner.nextLine().trim();
        return value.equalsIgnoreCase("Y") || value.equalsIgnoreCase("YES");
    }

    private void printCreatureTable(List<CreatureResponse> creatures) {
        if (creatures.isEmpty()) {
            System.out.println("No creatures found.");
            return;
        }
        System.out.printf("%-5s %-18s %-18s %-10s %-18s %-12s%n",
                "ID", "Name", "Species", "Status", "Habitat", "Zone");
        System.out.println("--------------------------------------------------------------------------------");
        for (CreatureResponse c : creatures) {
            System.out.printf("%-5d %-18s %-18s %-10s %-18s %-12s%n",
                    c.id(), clip(c.name(), 18), clip(c.species(), 18), c.status(),
                    clip(c.habitatName(), 18), clip(nullToBlank(c.habitatZone()), 12));
        }
    }

    private void printCreatureDetails(CreatureResponse c) {
        System.out.printf("%-14s %d%n", "ID:", c.id());
        System.out.printf("%-14s %s%n", "Name:", c.name());
        System.out.printf("%-14s %s%n", "Species:", c.species());
        System.out.printf("%-14s %s%n", "Status:", c.status());
        System.out.printf("%-14s %s%n", "Habitat:", c.habitatName());
        System.out.printf("%-14s %s%n", "Zone:", nullToBlank(c.habitatZone()));
        System.out.printf("%-14s %s%n", "Removed at:", nullToBlank(c.removedAt()));
    }

    private void printObservationsTable(List<ObservationResponse> observations) {
        if (observations.isEmpty()) {
            System.out.println("No observations recorded for this creature.");
            return;
        }
        System.out.printf("%-5s %-20s %-26s %-50s%n", "ID", "Author", "Observed At", "Note");
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        for (ObservationResponse observation : observations) {
            System.out.printf("%-5d %-20s %-26s %-50s%n",
                    observation.id(),
                    clip(observation.authorFullName(), 20),
                    clip(observation.observedAt(), 26),
                    clip(observation.note(), 50));
        }
    }

    private void printFeedingTable(List<FeedingItemResponse> feedings) {
        if (feedings.isEmpty()) {
            return;
        }
        System.out.printf("%-5s %-18s %-18s %-8s %-16s %-35s%n",
                "ID", "Creature", "Habitat", "Time", "Food", "Instructions");
        System.out.println("------------------------------------------------------------------------------------------------------");
        for (FeedingItemResponse f : feedings) {
            System.out.printf("%-5d %-18s %-18s %-8s %-16s %-35s%n",
                    f.creatureId(),
                    clip(f.creatureName(), 18),
                    clip(f.habitatName(), 18),
                    f.feedingTime(),
                    clip(f.food(), 16),
                    clip(nullToBlank(f.instructions()), 35));
        }
    }

    private void printUserTable(List<UserResponse> users) {
        if (users.isEmpty()) {
            System.out.println("No system users found.");
            return;
        }
        System.out.printf("%-5s %-18s %-22s %-28s %-14s %-8s %-18s%n",
                "ID", "Username", "Full Name", "Email", "Phone", "Active", "Roles");
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        for (UserResponse user : users) {
            System.out.printf("%-5d %-18s %-22s %-28s %-14s %-8s %-18s%n",
                    user.id(),
                    clip(user.username(), 18),
                    clip(user.fullName(), 22),
                    clip(user.email(), 28),
                    clip(user.phone(), 14),
                    user.active(),
                    clip(String.join(",", user.roles()), 18));
        }
    }

    private String clip(String value, int maxLength) {
        String safe = nullToBlank(value);
        if (safe.length() <= maxLength) {
            return safe;
        }
        if (maxLength <= 3) {
            return safe.substring(0, maxLength);
        }
        return safe.substring(0, maxLength - 3) + "...";
    }

    private String nullToBlank(String value) {
        return value == null ? "" : value;
    }

    private static String trimTrailingSlash(String value) {
        String result = value;
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    private record CreateCreatureRequest(String name, String species, Long habitatId, String status) {
    }

    private record RenameCreatureRequest(String newName) {
    }

    private record CreatureResponse(
            Long id,
            String name,
            String species,
            String status,
            Long habitatId,
            String habitatName,
            String habitatZone,
            String removedAt,
            String createdAt,
            String updatedAt
    ) {
    }

    private record RenameCreatureResponse(Long id, String oldName, String newName, String habitatName, String message) {
    }

    private record DeleteCreatureResponse(Long id, String name, String status, String removedAt, String message) {
    }

    private record ObservationResponse(Long id, String authorFullName, String authorUsername, String observedAt, String note) {
    }

    private record CreatureObservationsResponse(CreatureResponse creature, List<ObservationResponse> observations) {
    }

    private record FeedingItemResponse(
            Long scheduleId,
            Long creatureId,
            String creatureName,
            String species,
            String habitatName,
            String feedingTime,
            String food,
            String instructions
    ) {
    }

    private record FeedingLookupResponse(String time, String message, List<FeedingItemResponse> feedings) {
    }

    private record UserResponse(
            Long id,
            String username,
            String fullName,
            String email,
            String phone,
            boolean active,
            List<String> roles
    ) {
    }

    private record ErrorResponse(String timestamp, int status, String error, String message, Map<String, String> fields) {
    }
}
