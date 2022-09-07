import React, {useEffect, useMemo} from 'react';
import useAxios from "../../hooks/useAxios";
import Api from "../../api";
import HtmlForm from "../Forms/HtmlForm";
import {useForm} from "react-hook-form";
import {extractListFromAPI} from "../../utils";

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
    const [template, errorTemplate, loadingTemplate, axiosFetchTemplate] = useAxios();
    const [attributeValues, error, loading, axiosFetch] = useAxios();

    const fields = useMemo( () => prepareFields(template), [template] );
    const methods = useForm();

    const getTemplate = () => {
        const templateLink = group?._links?.group_template?.href;

        if ( templateLink ) {
            axiosFetchTemplate({
                axiosInstance: Api,
                method: 'get',
                url: group._links.group_template.href
            }).then(() => getPupilAttributeValues());
        }
    }

    const getPupilAttributeValues = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'get',
            url: `/pupils/${pupilId}/groups/${group.id}/attributes`
        }).then((pupilAttributes) => {
            extractListFromAPI( pupilAttributes, 'attributeValueList', value => {
                methods.setValue(`attribute-${group.templateId}-${value.attribute.id}`, value.value);
                return value;
            });
        });
    }

    const updatePupilAttributeValues = (data) => {
        // convert the data to be in ([attributeId]: value) structure
        data = Object.fromEntries(Object.entries(data).map(entry => {
           return [
               entry[0].replace(`attribute-${group.templateId}-`, ''),
               entry[1]
           ];
        }));

        axiosFetch({
            axiosInstance: Api,
            method: 'post',
            url: `/pupils/${pupilId}/groups/${group.id}/attributes`,
            data: { ...data },
        });
    }

    useEffect(() => {
        getTemplate();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    return (
        <>
            {!errorTemplate && template && (
                <HtmlForm
                    formProps={methods}
                    fields={fields}
                    loading={loading || loadingTemplate}
                    submitCallback={updatePupilAttributeValues}
                    submitLabel="Update"
                />
            )}
        </>
    )
}