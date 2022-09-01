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
        }
    ]
}