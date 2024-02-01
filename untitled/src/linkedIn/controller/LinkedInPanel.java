package linkedIn.controller;

import implementation.graph.AdjacencyMapGraph;
import implementation.graph.Vertex;
import linkedIn.model.Connection;
import linkedIn.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class LinkedInPanel {
    private AdjacencyMapGraph<User, Integer> graph;
    private Vertex<User> currentUser = null;
    private UserPanel userPanel = null;
    private Scanner sc;

    public LinkedInPanel() {
        graph = new AdjacencyMapGraph<>(false);
        sc = new Scanner(System.in).useDelimiter("\n");
    }

    public void start() {
        int command = sc.nextInt();
        while (true) {
            switch (command) {
                case 1 -> {
                    signup();
                    printMainMenu();
                }
                case 2 -> {
                    if (!signIn()) {
                        System.out.println("Invalid ID!");
                        printMainMenu();
                    } else {
                        userManager();
                    }
                }
                case 3 -> System.exit(0);
            }
            command = sc.nextInt();
        }
    }

    private static void printMainMenu() {
        System.out.println("""
                >[1] Sign up
                >[2] Sign in
                >[3] Exit
                """);
    }

    //--------------------------------------------------------------
    private void userManager() {
        printUserMenu();
        int command = sc.nextInt();
        while (command != 6) {
            switch (command) {
                case 1 -> {
                    printUserInformation();
                }
                case 2 -> {
                    //update
                }
                case 3 -> {
                    printSuggestions(suggestionWithLevel(),"Suggestions with level");
                }
                case 4 -> {
                    printSuggestions(sortedSuggestionsWithPriority(),"Suggestions with priority");
                }
                case 5 -> {
                    //suggestion
                }
            }
            printUserMenu();
            command = sc.nextInt();
        }
        printMainMenu();
    }


    private void printUserMenu() {
        System.out.println("""
                [1] Print information
                [2] Update information
                [3] Suggestions with level
                [4] Suggestions with priority
                [5] Suggestions with specific feature
                [6] Back
                """);
    }

    private void printSuggestions(ArrayList<Connection> suggestion,String string) {
        System.out.println(">>>>> "+string+" <<<<<");
        for (Connection c:suggestion){
            System.out.println(c.getUser().toString());
            System.out.println("------------------------------------------");
        }
    }

    private void printUserInformation(){
        System.out.println("===================================");
        System.out.println(currentUser.toString());
        System.out.println("===================================");

    }

    //----------------------------------------------------------------------
    private void signup() {
        System.out.println("-> Enter your ID :");
        String id = sc.next();
        if (checkID(id)) {
            System.out.println("Invalid ID!");
            signup();
        } else {
            System.out.println("-> Enter your full name :");
            String name = sc.next();

            System.out.println("-> Enter date of your birthday : (yyyy/MM/dd)");
            String birth = sc.next();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

            System.out.println("-> Enter universityLocation :");
            String universityLocation = sc.next();

            System.out.println("-> Enter field :");
            String field = sc.next();

            System.out.println("-> Enter workplace :");
            String workplace = sc.next();

            System.out.println("-> Enter your specialties :");
            String specialties = sc.next();
            String[] string = specialties.split(" ");
            List<String> list = new ArrayList<>(List.of(string));

            User newUser = new User(id, name, LocalDate.parse(birth, formatter), universityLocation, field, workplace, list, null);
            graph.insertVertex(newUser);

            System.out.println("You Signup Successfully!");
        }
    }

    /*
      This method used for checking the id of new user that must be unique
      or for finding id of a user that want to sign in
      if it doesn't found that id returns null.
     */
    private boolean checkID(String id) {
        for (Vertex<User> vertex : graph.vertices()) {
            if (vertex.getElement().getId().equals(id)) {
                return true;
            }
        }
        return false;
    }
    //----------------------------------------------------------------------

    /*
     This method used for finding user with input id
    */
    private Vertex<User> findUser(String id) {
        for (Vertex<User> vertex : graph.vertices()) {
            if (vertex.getElement().getId().equals(id))
                return vertex;
        }
        return null;
    }

    private boolean signIn() {
        System.out.println("-> Enter your ID :");
        String id = sc.next();
        if (!checkID(id)) {
            return false;
        }
        this.currentUser = findUser(id);
        this.userPanel = new UserPanel(currentUser);
        return true;
    }

    //====================================== list of connections (with level 1)=============================
    public ArrayList<Connection> getConnection() {
        Set<Vertex<User>> known = new HashSet<>();
        Map<Vertex<User>, Integer> map = new HashMap<>();
        graph.BFS(currentUser, known, map);
        ArrayList<Connection> connections = new ArrayList<>();
        for (Map.Entry<Vertex<User>, Integer> e : map.entrySet()) {
            if (e.getValue() == 1)
                connections.add(new Connection(e.getKey().getElement(), e.getValue()));
        }
        return connections;
    }

    // ========================== suggestion (further vertices) =====================================
    public ArrayList<Connection> suggestionWithLevel() {
        Set<Vertex<User>> known = new HashSet<>();
        Map<Vertex<User>, Integer> map = new HashMap<>();
        graph.BFS(currentUser, known, map);
        ArrayList<Connection> suggestion = new ArrayList<>();
        for (Map.Entry<Vertex<User>, Integer> e : map.entrySet()) {
            if (e.getValue() != 1)
                suggestion.add(new Connection(e.getKey().getElement(), e.getValue()));
        }
        Collections.sort(suggestion, new Comparator<Connection>() {
            @Override
            public int compare(Connection o1, Connection o2) {
                if (o1.getLevel()== o2.getLevel())
                    return 0;
                if (o1.getLevel()<o2.getLevel())
                    return -1;
                return 1;
            }
        });
        return suggestion;
    }

    // ========================== suggest with priority ===========================
    public ArrayList<Connection> sortedSuggestionsWithPriority() {
        return userPanel.prioritize(suggestionWithLevel());
    }

}
