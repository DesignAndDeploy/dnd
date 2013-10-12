package edu.teco.dnd.tests;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import edu.teco.dnd.util.FutureNotifier;


public class FutureNotifierMatchers {
	public static IsDone done() {
		return new IsDone();
	}

	public static IsSuccessful successful() {
		return new IsSuccessful();
	}

	public static IsFailed failed() {
		return new IsFailed();
	}

	public static <T> HasResult<T> hasResult(final T result) {
		return new HasResult<T>(result);
	}

	public static <T> HasResultThat<T> hasResultThat(final Matcher<? super T> result) {
		return new HasResultThat<T>(result);
	}

	public static HasCause hasCause(final Throwable cause) {
		return new HasCause(cause);
	}

	public static HasCauseThat hasCauseThat(final Matcher<? super Throwable> cause) {
		return new HasCauseThat(cause);
	}

	public static class IsDone extends TypeSafeMatcher<FutureNotifier<?>> {
		@Override
		public void describeTo(final Description description) {
			description.appendText("a finished FutureNotifier");
		}

		@Override
		public boolean matchesSafely(final FutureNotifier<?> futureNotifier) {
			return futureNotifier.isDone();
		}
	}

	public static class IsSuccessful extends TypeSafeMatcher<FutureNotifier<?>> {
		@Override
		public void describeTo(final Description description) {
			description.appendText("a successful FutureNotifier");
		}

		@Override
		public boolean matchesSafely(final FutureNotifier<?> futureNotifier) {
			return futureNotifier.isSuccess();
		}
	}

	public static class IsFailed extends TypeSafeMatcher<FutureNotifier<?>> {
		@Override
		public void describeTo(final Description description) {
			description.appendText("a failed FutureNotifier");
		}

		@Override
		public boolean matchesSafely(final FutureNotifier<?> futureNotifier) {
			return futureNotifier.isDone() && !futureNotifier.isSuccess();
		}
	}

	public static class HasResult<T> extends TypeSafeMatcher<FutureNotifier<? extends T>> {
		private final T result;

		public HasResult(final T result) {
			this.result = result;
		}

		@Override
		public void describeTo(final Description description) {
			description.appendText("a successful FutureNotifier with result ");
			description.appendValue(result);
		}

		@Override
		public boolean matchesSafely(FutureNotifier<? extends T> futureNotifier) {
			if (!futureNotifier.isSuccess()) {
				return false;
			}
			if (result == null) {
				return futureNotifier.getNow() == null;
			} else {
				return result.equals(futureNotifier.getNow());
			}
		}
	}

	public static class HasResultThat<T> extends TypeSafeMatcher<FutureNotifier<? extends T>> {
		private final Matcher<? super T> result;

		public HasResultThat(final Matcher<? super T> result) {
			this.result = result;
		}

		@Override
		public void describeTo(final Description description) {
			description.appendText("a successful FutureNotifier with a result that ");
			description.appendDescriptionOf(result);
		}

		@Override
		public boolean matchesSafely(final FutureNotifier<? extends T> futureNotifier) {
			if (!futureNotifier.isDone()) {
				return false;
			}
			if (!futureNotifier.isSuccess()) {
				return false;
			}
			return result.matches(futureNotifier.getNow());
		}
	}

	public static class HasCause extends TypeSafeMatcher<FutureNotifier<?>> {
		private final Throwable cause;

		public HasCause(final Throwable cause) {
			this.cause = cause;
		}

		@Override
		public void describeTo(final Description description) {
			description.appendText("a failed FutureNotifier with cause ");
			description.appendValue(cause);
		}

		@Override
		public boolean matchesSafely(final FutureNotifier<?> futureNotifier) {
			if (!futureNotifier.isDone()) {
				return false;
			}
			if (futureNotifier.isSuccess()) {
				return false;
			}

			if (cause == null) {
				return futureNotifier.cause() == null;
			} else {
				return cause.equals(futureNotifier.cause());
			}
		}
	}

	public static class HasCauseThat extends TypeSafeMatcher<FutureNotifier<?>> {
		private final Matcher<? super Throwable> cause;

		public HasCauseThat(final Matcher<? super Throwable> cause) {
			this.cause = cause;
		}

		@Override
		public void describeTo(final Description description) {
			description.appendText("a failed FutureNotifier with a cause that ");
			description.appendDescriptionOf(cause);
		}

		@Override
		public boolean matchesSafely(final FutureNotifier<?> futureNotifier) {
			if (!futureNotifier.isDone()) {
				return false;
			}
			if (futureNotifier.isSuccess()) {
				return false;
			}
			return cause.matches(futureNotifier.cause());
		}
	}
}
