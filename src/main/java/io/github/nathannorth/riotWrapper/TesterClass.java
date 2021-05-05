package io.github.nathannorth.riotWrapper;

import io.github.nathannorth.riotWrapper.clients.RiotDevelopmentAPIClient;
import io.github.nathannorth.riotWrapper.objects.ValRegion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TesterClass {
    public static void main(String[] args) {

        RiotDevelopmentAPIClient client = new RiotDevelopmentAPIClient(getKeys().get(0));

        System.out.println(client.getValStatus(ValRegion.BRAZIL).block().toString());
    }

    //keys.txt is stored in root dir and holds instance-specific data (eg. bot token)
    private static List<String> keys = null;
    public static List<String> getKeys() {
        if (keys == null) {
            try {
                keys = Files.readAllLines(Paths.get("./keys.txt"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //filter out things we commented out in our keys
            for (int i = keys.size() - 1; i >= 0; i--) {
                if (keys.get(i).indexOf('#') == 0) keys.remove(i);
            }
        }
        return keys;
    }
}
