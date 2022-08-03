import React from 'react';
import { Form } from "react-bootstrap";
import {Controller} from "react-hook-form";

export default function Checkbox({ field: settings, control, hasError }) {
    return (
        <div className={hasError ? 'is-invalid' : ''}>
            {settings.options && settings.options.map( option =>
                <Controller
                    key={option.value}
                    control={control}
                    name={settings.id}
                    rules={settings.rules}
                    render={({ field } ) =>
                        <Form.Check
                            id={`${settings.id}-${option.value}`}
                            type={settings.type}
                            label={option.label}
                            defaultChecked={field.value && field.value === option.value}
                            {...field}
                            value={option.value}
                            {...settings?.bsProps}
                            isInvalid={hasError}
                        />
                    }
                />
            )}
        </div>
    );
}
