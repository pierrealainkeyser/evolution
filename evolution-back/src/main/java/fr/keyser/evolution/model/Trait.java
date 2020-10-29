package fr.keyser.evolution.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Trait {
	COOPERATION(true), FAT_TISSUE(true), FORAGING(true), SCAVENGER(true), HORNS(true), QUILLS(false), VENOM(false),
	FERTILE(true), LONGNECK(true), CARNIVOROUS(true), INTELLIGENT(true),
	AMBUSH(true), BURROWING(true), CLIMBING(true), HARD_SHELL(true), DEFENSIVE_HERDING(true), SYMBIOSIS(true),
	WARNING_CALL(true), PACK_HUNTING(true), PEST(false);

	private final boolean core;

	private Trait(boolean core) {
		this.core = core;
	}

	public boolean isCore() {
		return core;
	}
	
	public static List<Trait> core(){
		return Arrays.asList(Trait.values()).stream().filter(Trait::isCore).collect(Collectors.toList());
	}
}
