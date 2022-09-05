import {Controller} from "react-hook-form";
import {Form, Stack} from "react-bootstrap";

export default function Range({ field: settings, control, hasError }) {
    return (
        <Controller
            name={settings.id}
            control={control}
            rules={settings.rules}
            render={({ field }) => (
                <Stack direction="horizontal" gap={2}>
                    <small>{field.value}</small>
                    <Form.Range {...field} value={field.value || settings.defaultValue || ''} {...settings?.bsProps} isInvalid={hasError} />
                </Stack>
            )}
        />
    )
}