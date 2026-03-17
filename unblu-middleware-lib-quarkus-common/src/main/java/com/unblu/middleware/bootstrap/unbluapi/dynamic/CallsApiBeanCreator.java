package com.unblu.middleware.bootstrap.unbluapi.dynamic;

import com.unblu.middleware.bootstrap.unbluapi.UnbluApiFactory;
import io.quarkus.arc.BeanCreator;
import io.quarkus.arc.SyntheticCreationalContext;

public class CallsApiBeanCreator implements BeanCreator<Object> {

    @Override
    public Object create(SyntheticCreationalContext<Object> ctx) {
        UnbluApiFactory creator = ctx.getInjectedReference(UnbluApiFactory.class);
        return creator.create("com.unblu.webapi.jersey.v4.api.CallsApi");
    }
}

