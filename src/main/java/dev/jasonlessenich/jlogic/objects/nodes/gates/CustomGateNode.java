package dev.jasonlessenich.jlogic.objects.nodes.gates;

import dev.jasonlessenich.jlogic.objects.nodes.Evaluable;
import dev.jasonlessenich.jlogic.objects.nodes.gates.custom.JGate;
import dev.jasonlessenich.jlogic.objects.pins.naming_strategies.CustomNamingStrategy;
import dev.jasonlessenich.jlogic.utils.Point;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CustomGateNode extends GateNode implements Evaluable {
	private final JGate.Table table;

	public CustomGateNode(@Nonnull Point point, @Nonnull JGate gate, @Nonnull JGate.Table table) {
		super(point, new CustomNamingStrategy(gate.getCustomNames(), gate.getNamingStrategy()), table.getInputCount(), table.getOutputCount(), gate.getSymbol());
		this.table = table;
	}

	@Override
	public boolean evaluate(@Nonnull List<Boolean> inputs) {
		final String binaryString = inputs.stream().map(b -> b ? "1" : "0")
				.collect(Collectors.joining(", "));
		log.info("Evaluating {} with table {}", binaryString, table);
		final Boolean result = table.getMap().get(binaryString);
		if (result == null)
			log.warn("No result for [" + binaryString + "] in table map: " + table.getMap());
		return result != null && result;
	}
}
