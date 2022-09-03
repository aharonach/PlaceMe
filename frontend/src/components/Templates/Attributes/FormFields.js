export default function FormFields() {
    return [
        {
            id: "name",
            label: "Name",
            rules: { required: true },
        },
        {
            id: "description",
            label: "Description",
            type: "textarea",
        },
        {
            id: "type",
            label: "Type",
            type: "radio",
            options: [
                { value: "range", label: "Range" }
            ],
        },
        {
            id: "priority",
            label: "Priority",
            type: "number",
            rules: {
                valueAsNumber: true
            },
            bsProps: {
                min: 1,
            }
        }
    ]
}
