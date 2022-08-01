export default function FormFields() {
    return [
        {
            id: "name",
            label: "Name",
            rules: { required: "This field is required." },
        },
        {
            id: "description",
            label: "Description",
            rules: { required: "This field is required." },
        }
    ];
}