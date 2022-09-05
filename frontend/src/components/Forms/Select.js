import React from "react";
import {Form} from "react-bootstrap";
import {Controller} from "react-hook-form";

export default function Select({ field: settings, control, hasError }) {
    return (
        <Controller
            name={settings.id}
            control={control}
            rules={settings.rules}
            render={({ field }) => {
                return (
                    <Form.Select {...field} {...settings?.bsProps} isInvalid={hasError}>
                        {settings.options?.map((option, index) => (
                            <option
                                key={option.value || `placeholder-${index}`}
                                value={option.value}
                            >
                                {option.label}
                            </option>
                        ))}
                    </Form.Select>
                );
            }}
        />
    )
}