import useDynamicOptions from "../../hooks/useDynamicOptions";

export default function FormFields() {
    const templates = useDynamicOptions( '/templates', 'templateList' );

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
            id: "templateId",
            label: "Template",
            type: "select",
            options: templates,
            rules: {
                required: true
            }
        }
    ];
}