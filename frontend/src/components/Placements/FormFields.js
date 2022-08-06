import useDynamicOptions from "../../hooks/useDynamicOptions";

export default function FormFields() {
    const groups = useDynamicOptions( '/groups', 'groupList' );

    return [
        {
            id: 'name',
            label: 'Name',
            rules: { required: true },
        },
        {
            id: 'numberOfClasses',
            label: 'Number of classes',
            type: "number",
            rules: {
                required: true,
                min: 2,
                validate: v => /^\d+$/.test(v) || "Only digits."
            },
        },
        {
            id: "groupId",
            label: "Group",
            type: "select",
            options: groups,
            rules: {
                required: false
            }
        }
    ];
}