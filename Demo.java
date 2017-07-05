public class Demo {
	@FunctionalInterface
    public static interface Yieldable {
        public void yield(int i);
    }

    public static class Yielder {
        volatile Thread t;
        volatile Yieldable yieldable;
        volatile Object lock = new Object();

        synchronized public boolean next(Yieldable yieldable) {
            this.yieldable = yieldable;

            try {
                synchronized (lock) {
                    lock.notify();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return t.isAlive();
        }

        void yield(int i) throws InterruptedException {
            yieldable.yield(i);

            synchronized (lock) {
                lock.wait();
            }
        }
    }

    @FunctionalInterface
    public static interface Generator {
        public void create(Yielder yielder)throws InterruptedException;
    }


    public static Yielder generateGenerator(Generator g) {
        Yielder yielder = new Yielder();
        yielder.t = new Thread(() -> {
            try {
                synchronized (yielder.lock) {
                    yielder.lock.wait();
                }
                g.create(yielder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        yielder.t.start();
        return yielder;
    }

    public static Yielder fib(int n) {

        return generateGenerator((yielder) -> {
            int cur = 1;
            int prev = 0;

            for (int i = 0; i < n; i++) {
                System.out.println("Before yield");
                yielder.yield(cur);

                int temp = cur;
                cur = prev + cur;
                prev = temp;
            }
        });
    }

	public static void main(String... args) {
		Yielder dux = fib(9);

        while (dux.next(curr -> System.out.println(curr)));
	}
}