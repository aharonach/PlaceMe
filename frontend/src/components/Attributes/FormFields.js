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
        },
        {
            id: "priority",
            label: "Priority",
            type: "number",
            bsProps: {
                min: 1,
            }
        }
    ]
}
