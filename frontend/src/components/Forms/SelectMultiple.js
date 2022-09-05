import {Controller} from "react-hook-form";
import React from "react";
import Select from "react-select";

export default function SelectMultiple({ field: settings, control, hasError }) {
    return (
        <Controller
            name={settings.id}
            control={control}
            defaultValue={settings.value}
            rules={settings.rules}
            render={({ field: { onChange, value, ref }}) => (
                <Select
                    isLoading={!settings.options}
                    isSearchable={true}
                    isMulti
                    inputRef={ref}
                    options={settings.options}
                    value={settings.options?.filter(c => value?.includes(c.value))}
                    onChange={val => onChange(val.map(v => v.value))}
                    className={hasError ? 'is-invalid' : ''}
                    {...settings?.bsProps}
                />
            )}
        />
    )
}