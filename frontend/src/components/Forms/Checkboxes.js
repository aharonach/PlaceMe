import React, {useState} from 'react';
import { Form } from "react-bootstrap";
import {Controller, useController} from "react-hook-form";

export default function Checkboxes({ field: settings, control, hasError }) {
    const name = settings.id;
    const { field } = useController({ control, name });
    const [value, setValue] = useState(field.value || [] );

    const onChange = (e) => {
        let valueCopy = [...value];

        // update checkbox value
        if ( e.target.checked ) {
            ! valueCopy.includes(e.target.value) && valueCopy.push(e.target.value);
        } else {
            valueCopy = valueCopy.filter( val => val !== e.target.value );
        }

        // send data to react hook form
        field.onChange(valueCopy);

        // update local state
        setValue(valueCopy);
    };

    return (
        <Controller
            control={control}
            name={name}
            rules={settings.rules}
            render={({ field } ) => {
                return (
                    <div className={hasError ? 'is-invalid' : ''}>
                        {settings.options && settings.options.map( option => {
                                const checked = field.value && field.value.find( val => val === option.value.toString() );

                                return (
                                    <Form.Check
                                        key={option.value}
                                        id={`${settings.id}-${option.value}`}
                                        type={settings.type}
                                        label={option.label}
                                        defaultChecked={checked}
                                        {...field}
                                        onChange={onChange}
                                        value={option.value}
                                        {...settings?.bsProps}
                                        isInvalid={hasError}
                                    />
                                )
                            }
                        )}
                    </div>
                )
            }}
        />
    )
}
