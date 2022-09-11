import React, {useState} from 'react';
import useAxios from "../../hooks/useAxios";
import HtmlForm from "../Forms/HtmlForm";
import {useForm} from "react-hook-form";
import {extractListFromAPI} from "../../utils";
import useFetchRecord from "../../hooks/useFetchRecord";
import {Alert} from "react-bootstrap";

const prepareFields = (template) => {
    const fields = [];

    template?.attributes?.forEach( attribute => {
        fields.push({
            id: `attribute-${template.id}-${attribute.id}`,
            label: attribute.name,
            type: 'range',
            description: attribute.description,
            bsProps: { step: 0.1, min: 1, max: 5 },
            rules: { required: true, min: 1, max: 5 },
        });
    });

    return fields;
}

export default function Attributes({ pupilId, group }) {
    const form = useForm();
    const [fields, setFields] = useState([]);
    const [updated, setUpdated] = useState(false);
    // eslint-disable-next-line no-unused-vars
    const [attributeValues, error, loading, fetch] = useAxios();
    const [template, errorTemplate, loadingTemplate] = useFetchRecord({
        fetchUrl: `/templates/${group.templateId}`,
        thenCallback: async (template) => {
            setFields(prepareFields(template));
            await getPupilAttributeValues();
        }
    });

    const getPupilAttributeValues = async () => {
        const pupilAttributes = await fetch({
            method: 'get',
            url: `/pupils/${pupilId}/groups/${group.id}/attributes`
        });

        // Set values again
        extractListFromAPI( pupilAttributes, 'attributeValueList', value => {
            form.setValue(`attribute-${group.templateId}-${value.attribute.id}`, value.value);
            return value;
        });
    }

    const updatePupilAttributeValues = async (data) => {
        setUpdated(false);

        // convert the data to be in ([attributeId]: value) structure
        data = Object.fromEntries(Object.entries(data).map(entry => {
           return [
               entry[0].replace(`attribute-${group.templateId}-`, ''),
               entry[1]
           ];
        }));

        await fetch({
            method: 'post',
            url: `/pupils/${pupilId}/groups/${group.id}/attributes`,
            data: { ...data },
        });
        setUpdated(true);
    }

    return (
        <>
            {!errorTemplate && !error && template && (<>
                {updated && <Alert variant="success">Attributes updated successfully</Alert> }
                <HtmlForm
                    formProps={form}
                    fields={fields}
                    loading={loading || loadingTemplate}
                    submitCallback={updatePupilAttributeValues}
                    submitLabel="Update"
                    submitClass="w-100"
                />
            </>)}
        </>
    )
}