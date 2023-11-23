package dev.jasonlessenich.jlogic.objects.nodes.gates;

import dev.jasonlessenich.jlogic.objects.nodes.Evaluable;
import dev.jasonlessenich.jlogic.objects.nodes.gates.custom.JGate;
import dev.jasonlessenich.jlogic.objects.pins.naming_strategies.CustomNamingStrategy;
import dev.jasonlessenich.jlogic.utils.Point;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CustomGateNode extends GateNode implements Evaluable {
	@Nonnull
	private final JGate gate;
	@Nonnull
	private final JGate.Table table;

	public CustomGateNode(@Nonnull Point point, @Nonnull JGate gate, @Nonnull JGate.Table table) {
		super(
				point,
				new CustomNamingStrategy(gate.getInputNames(), gate.getInputNamingStrategy()),
				new CustomNamingStrategy(gate.getOutputNames(), gate.getOutputNamingStrategy()),
				table.getInputCount(),
				table.getOutputCount(),
				gate.getSymbol()
		);
		this.gate = gate;
		this.table = table;
	}

	@Override
	public boolean[] evaluate(@Nonnull List<Boolean> inputs) {
		final String binaryString = inputs.stream().map(b -> b ? "1" : "0")
				.collect(Collectors.joining());
		final boolean[] result = table.getDefinition().get(binaryString);
		log.info("Evaluated {} [{}] => {}", gate.getName(), binaryString, Arrays.toString(result));
		if (result == null)
			log.error("No result for [{}] in table map: {}", binaryString, table.getDefinition());
		return result;
	}
}
