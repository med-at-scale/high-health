$ ->
  cParseInt = (s) ->
    v = parseInt(s)
    if isNaN(v)
      throw "NaN"
    else
      v
  cParseFloat = (s) ->
    v = parseFloat(s)
    if isNaN(v)
      throw "NaN"
    else
      v
  $("#searchVariants").submit (f) =>
    r = {}
    $(f.target.elements).map (i, e) =>
      if e.type != "submit"
        if ! $(e).attr("data-type")
          v = if (e.value != '')
            try
              cParseInt(e.value)
            catch e1
              try
                cParseFloat(e.value)
              catch e2
                e.value
          else
            null
          r[e.name] = v
        else
          r[e.name] = JSON.parse(e.value)

    jsRoutes.controllers.variants.VariantController.searchVariants().ajax(
      contentType: "application/json; charset=utf-8"
      dataType: "json"
      data: JSON.stringify(r)
    )
    .done((e) -> $("#result").text(e.responseText))
    .fail((e) -> $("#result").text(e.responseText))
    f.stopImmediatePropagation()
    false