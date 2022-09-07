import {Controller} from "react-hook-form";
import {Form} from "react-bootstrap";

export default function File({ field: settings, control, hasError }) {
    return (
        <Controller
            name={settings.id}
            control={control}
            rules={settings.rules}
            render={({ field }) => {
                return <Form.Control
                    type="file"
                    isInvalid={hasError}
                    onChange={e => field.onChange(e.target.files)}
                    {...settings?.bsProps}
                />
            }}
        />
    )
}