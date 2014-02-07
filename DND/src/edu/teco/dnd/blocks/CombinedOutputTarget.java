package edu.teco.dnd.blocks;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This class can be used to attach multiple OutputTargets to a single {@link Output}.
 * 
 * @param <T>
 *            the type of values accepted by this OutputTarget
 */
public class CombinedOutputTarget<T extends Serializable> implements OutputTarget<T> {
	private final Set<OutputTarget<? super T>> targets;

	public CombinedOutputTarget(final Collection<OutputTarget<? super T>> targets) {
		this.targets = Collections.unmodifiableSet(new HashSet<OutputTarget<? super T>>(targets));
	}

	public CombinedOutputTarget(final OutputTarget<? super T>... targets) {
		final Set<OutputTarget<? super T>> targetsSet = new HashSet<OutputTarget<? super T>>(targets.length);
		for (final OutputTarget<? super T> target : targets) {
			targetsSet.add(target);
		}
		this.targets = Collections.unmodifiableSet(targetsSet);
	}

	@Override
	public synchronized void setValue(final T value) {
		for (final OutputTarget<? super T> target : targets) {
			target.setValue(value);
		}
	}
}
