const PROMISE = Symbol();
const RESOLVE = Symbol();

class ResolvablePromise {
  constructor() {
    this[PROMISE] = new Promise((resolve) => {
      this[RESOLVE] = resolve;
    });
  }

  get promise() {
    return this[PROMISE];
  }

  resolve(value) {
    this[RESOLVE](value);
  }
}

class PromiseHolder {
  constructor() {
    this[PROMISE] = null;
  }

  wait() {
    const resolvable = new ResolvablePromise();
    this[PROMISE] = resolvable;
    return resolvable.promise;
  }

  resolve(value) {
    const resolvable = this[PROMISE];
    if (resolvable)
      resolvable.resolve(value);
  }
}

export default new PromiseHolder();
