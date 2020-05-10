package fr.leomelki.loupgarou.classes;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import fr.leomelki.loupgarou.events.LGCustomItemChangeEvent;
import fr.leomelki.loupgarou.roles.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class LGCustomItems {
	static final HashMap<Class<? extends Role>, HashMap<String, Material>> mappings = new HashMap<>();
	static final String JSON_FILE = "custom_items.json";

	private static Object readCustomItemsJSON() throws Exception {
		final InputStream stream = LGCustomItems.class.getClassLoader().getResourceAsStream(LGCustomItems.JSON_FILE);
		final Reader reader = new InputStreamReader(stream);
		JSONParser jsonParser = new JSONParser();

		return jsonParser.parse(reader);
	}

	private static void addItem(final String roleName, HashMap<String, Material> currentMapping) {
		try {
			@SuppressWarnings("unchecked")
			final Class<? extends Role> matchingClass = (Class<? extends Role>) Class
					.forName("fr.leomelki.loupgarou.roles.R" + roleName);

			LGCustomItems.mappings.put(matchingClass, currentMapping);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	static {
		JSONObject parsedJson = null;

		try {
			parsedJson = (JSONObject) readCustomItemsJSON();
		} catch (Exception e) {
			throw new RuntimeException("Failed to parse JSON file" + LGCustomItems.JSON_FILE, e);
		}

		for (Object rawEntry : parsedJson.entrySet()) {
			final HashMap<String, Material> items = new HashMap<>();

			@SuppressWarnings("unchecked")
			final Entry<String, HashMap<String, Material>> entry = (Entry<String, HashMap<String, Material>>) rawEntry;
			final JSONObject currentProperties = (JSONObject) entry.getValue();
			final String roleName = entry.getKey();

			for (Object rawProperty : currentProperties.entrySet()) {
				@SuppressWarnings("unchecked")
				final Entry<String, String> property = (Entry<String, String>) rawProperty;
				final String currentName = property.getKey();
				final String currentMaterial = property.getValue();

				items.put(currentName, Material.valueOf(currentMaterial));
			}

			LGCustomItems.addItem(roleName, items);
		}
	}

	public static Material getItem(Role role) {
		return mappings.get(role.getClass()).get("");
	}

	public static Material getItem(LGPlayer player, List<String> constraints) {
		Bukkit.getPluginManager().callEvent(new LGCustomItemChangeEvent(player.getGame(), player, constraints));

		Collections.sort(constraints);
		HashMap<String, Material> mapps = mappings.get(player.getRole().getClass());
		
		// Lors du développement de rôles.
		if (mapps == null)
			return Material.AIR;

		StringJoiner sj = new StringJoiner("_");
		for (String s : constraints)
			sj.add(s);
		return mapps.get(sj.toString());
	}

	public static Material getItem(LGPlayer player) {
		return getItem(player, new ArrayList<String>());
	}

	public static void updateItem(LGPlayer lgp) {
		lgp.getPlayer().getInventory().setItemInOffHand(new ItemStack(getItem(lgp)));
		lgp.getPlayer().updateInventory();
	}

	public static void updateItem(LGPlayer lgp, List<String> constraints) {
		lgp.getPlayer().getInventory().setItemInOffHand(new ItemStack(getItem(lgp, constraints)));
		lgp.getPlayer().updateInventory();
	}

	@RequiredArgsConstructor
	public enum LGCustomItemsConstraints {
		INFECTED("infecte"), 
		MAYOR("maire"), 
		VAMPIRE_INFECTE("vampire-infecte"), 
		DEAD("mort");

		@Getter private final String name;
	}

}
