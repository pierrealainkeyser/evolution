package fr.keyser.fsm;

import static fr.keyser.fsm.impl.transition.TransitionSourceBuilder.choice;
import static fr.keyser.fsm.impl.transition.TransitionSourceBuilder.guard;
import static fr.keyser.fsm.impl.transition.TransitionSourceBuilder.route;
import static fr.keyser.fsm.impl.transition.TransitionSourceBuilder.to;

import org.junit.jupiter.api.Test;

import fr.keyser.fsm.impl.transition.StringBuilderTransitionVisitor;

public class TestStringBuilderTransitionVisitor {

	private static class NamedGuardEvent implements EventGuard {

		private final String name;

		public NamedGuardEvent(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

		@Override
		public boolean accept(AutomatInstance instance, AutomatEvent event) {
			return false;
		}

	}

	@Test
	void nominal() {

		StringBuilderTransitionVisitor visitor = new StringBuilderTransitionVisitor();

		FollowedTransitionSource r1 = route() //
				.on("a", guard().check(new NamedGuardEvent("testA"), to(this::getA)))//
				.on("b", guard().check(new NamedGuardEvent("testB"), to(this::getB))) //
				.on("c", to(this::getB)) //
				.get();

		visitor.visit(r1);

		System.out.println(visitor);
	}

	@Test
	void testChoice() {

		StringBuilderTransitionVisitor visitor = new StringBuilderTransitionVisitor();

		FollowedTransitionSource r1 = choice().when(new NamedGuardEvent("testA"), to(this::getA))
				.orElse(to(this::getB));

		visitor.visit(r1);

		System.out.println(visitor);
	}

	private State getA() {
		return new State("a");
	}

	private State getB() {
		return new State("b");
	}
}
