public class Demo {
	@FunctionalInterface
    public static interface Iterable {
        public void execute(int i);
    }

    public static class Iterator {
        volatile Thread t;
        volatile Iterable iterable;
        volatile Object lock = new Object();

        synchronized public boolean next(Iterable iterable) {
            this.iterable = iterable;

            try {
                synchronized (lock) {
                    lock.notify();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return t.isAlive();
        }

        void execute(int i) throws InterruptedException {
            iterable.execute(i);

            synchronized (lock) {
                lock.wait();
            }
        }
    }

    @FunctionalInterface
    public static interface Generator {
        public void create(Iterator iterator)throws InterruptedException;
    }


    public static Iterator generateGenerator(Generator g) {
        Iterator i = new Iterator();
        i.t = new Thread(() -> {
            try {
                synchronized (i.lock) {
                    i.lock.wait();
                }
                g.create(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        i.t.start();
        return i;
    }

    public static Iterator fib(int n) {

        return generateGenerator((iterator) -> {
            int cur = 1;
            int prev = 0;

            for (int i = 0; i < n; i++) {
                System.out.println("Before yield");
                iterator.execute(cur);

                int temp = cur;
                cur = prev + cur;
                prev = temp;
            }
        });
    }

	public static void main(String... args) {
		Iterator dux = fib(9);

        while (dux.next(curr -> System.out.println(curr)));
	}
}