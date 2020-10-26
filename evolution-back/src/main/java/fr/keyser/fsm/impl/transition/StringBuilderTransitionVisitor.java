package fr.keyser.fsm.impl.transition;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import fr.keyser.fsm.EventGuard;
import fr.keyser.fsm.FollowedTransitionVisitor;
import fr.keyser.fsm.State;

public class StringBuilderTransitionVisitor implements FollowedTransitionVisitor {

	private static class Buffer {

		private final String start;

		private final String end;

		private final List<String> elements = new ArrayList<>();

		private final String separator;

		private Buffer(String start, String end, String separator) {
			this.start = start;
			this.end = end;
			this.separator = separator;
		}

		public Buffer push(String val) {
			elements.add(val);
			return this;
		}

		public void append(String val) {
			int size = elements.size();
			int to = size - 1;
			String next = elements.get(to) + val;

			elements.set(to, next);
		}

		@Override
		public String toString() {
			return start + elements.stream().collect(Collectors.joining(separator)) + end;
		}
	}

	private final LinkedList<Buffer> buffers = new LinkedList<>();

	public StringBuilderTransitionVisitor() {
		buffers.add(new Buffer("", "", "").push(""));
	}

	@Override
	public void direct(State state) {
		Buffer b = new Buffer("", "", "").push("->" + state);
		buffers.add(b);

	}

	@Override
	public void guard(EventGuard guard) {
		Buffer b = new Buffer("(", ")", ", ").push(guard + " ? ");
		buffers.add(b);
	}

	@Override
	public void router() {
		Buffer b = new Buffer("{", "}", " | ");
		buffers.add(b);
	}

	@Override
	public void route(Object key) {
		buffers.peekLast().append("@" + key);

	}

	@Override
	public String toString() {
		return buffers.stream().map(Buffer::toString).collect(Collectors.joining(" + "));
	}

	@Override
	public void next() {
		buffers.peekLast().push("");
	}

	@Override
	public void list() {
		Buffer b = new Buffer("[", "]", ", ");
		buffers.add(b);
	}

	@Override
	public void end() {
		Buffer buffer = buffers.pollLast();

		buffers.peekLast().append(buffer.toString());

	}

}
