package com.jywoo.payment.client;

import javax.validation.Valid;

public abstract class PayClient<T extends Object, R extends Object> {
    public abstract R send(@Valid T t);
}
