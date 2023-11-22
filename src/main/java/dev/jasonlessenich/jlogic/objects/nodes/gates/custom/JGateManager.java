package dev.jasonlessenich.jlogic.objects.nodes.gates.custom;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.jasonlessenich.jlogic.objects.pins.naming_strategies.PinNamingStrategy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Getter
@Slf4j
public class JGateManager {
	private final List<JGate> gates;

	public JGateManager(@Nonnull Path... paths) throws IOException {
		final Gson gson = new GsonBuilder().enableComplexMapKeySerialization()
				.create();
		this.gates = new ArrayList<>();
		for (Path dir : paths) {
			if (!Files.exists(dir)) {
				log.error("Path does not exist: " + dir);
				continue;
			}
			try (Stream<Path> stream = Files.walk(dir)) {
				for (Path path : stream.toList()) {
					if (Files.isDirectory(path) || !path.toFile().getName().endsWith(".jgate")) continue;
					try (BufferedReader reader = Files.newBufferedReader(path)) {
						final JGate gate = gson.fromJson(reader, JGate.class);
						// get naming strategies to make sure they are valid
						gate.getInputNamingStrategy();
						gate.getOutputNamingStrategy();
						for (JGate.Table table : gate.getTableDefinition()) {
							if (table.getMap().size() != Math.pow(2, table.getInputCount())) {
								throw new IllegalArgumentException(
										"%s: Table map count does not match input count! Expected: %d, Got: %d"
												.formatted(path, (int) Math.pow(2, table.getInputCount()), table.getMap().size()));
							}
						}
						gates.add(gate);
						log.info("Loaded gate: {}, ({})", gate.getName(), path);
					}
				}
			}
		}
	}

}
